(ns server.api
  (:require
    [ring.middleware.resource               :refer [wrap-resource]]
    [ring.middleware.file                   :refer [wrap-file]]
    [liberator.core                         :refer [resource defresource]]
    [ring.middleware.content-type           :refer [wrap-content-type]]
    [ring.middleware.cors                   :refer [wrap-cors]]
    [ring.middleware.params                 :refer [wrap-params]]
    [ring.middleware.stacktrace             :refer [wrap-stacktrace]]
    [adzerk.env                             :as env]
    [cheshire.core                          :as json]
    [castra.middleware                      :refer [wrap-castra
                                                    wrap-castra-session]]
    [castra.core                            :refer [defrpc]]
    [compojure.core                         :refer [defroutes ANY]]))

(env/def
  RESOURCE_ROOT "/"
  FILE_ROOT     "/")

(defn body-as-string [ctx]
  (if-let [body   (get-in ctx [:request :body])]
    (condp instance? body
      java.lang.String body
      (slurp (io/reader body)))))

(defn parse-json [ctx key]
  (when ( #{:put :post} (get-in ctx [:request :request-method]))
    (try
      (if-let [body     (body-as-string ctx)]
          (let [data    (json/parse-string body true)]
          [false {key data}])
        {:message "No Body"})
      (catch Exception e
       ;(.printStackTrace e)
        {:message (format "IOException: %s" (.getMessage e))}))))

(defn check-content-type [ctx content-types]
  (if (#{:put :post} (get-in ctx [:request :request-method]))
    (or
      (some #{(get-in ctx [:request :headers "content-type"])}
            content-types)
      [false {:message "Unsuported Content-Type"}])
    true))

(defresource route
  :available-media-types  ["application/json"]
  :allowed-methods        [:post]
  :post!                  #(let [d        %
                                 id       (str (java.util.UUID/randomUUID))]
                              {::id id})
  :post-redirect?         false
  :new?                   true
  :handle-created         #(assoc {} :id  (::id %)))

(defrpc get-state []
  {:state "foo"})


(defroutes handler 
  (ANY "/foo" [] (resource :available-media-types ["text/html"]
                           :handle-ok "<html>Hello, Internet.</html>"))
  (ANY "/route" [] route)
  )

(def app (-> handler
              (wrap-castra          'server.api)
              (wrap-castra-session  "tLqIU72r6vB5glOF")
              (wrap-cors            #".*")

              (wrap-resource        RESOURCE_ROOT)
              (wrap-file            FILE_ROOT)
              (wrap-params)
              (wrap-content-type)
      ))
