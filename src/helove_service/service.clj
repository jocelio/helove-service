(ns helove-service.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

(defn home-page
  [request]
  (ring-resp/response "Hello World!"))

(defn get-videos
  [request]
  (http/json-response (map #(.getName %) (file-seq (clojure.java.io/file "/home/jocelio/Videos/pocoyo/")))))

(defn get-video [request] 
  (let [video (get-in request [:path-params :video])]
    {:status 200
   :headers {"Content-Type" "video/mp4"}
   :body (java.io.File. (.concat "/home/jocelio/Videos/pocoyo/" video))}))

  

(def common-interceptors [(body-params/body-params) http/html-body])


;; Terse/Vector-based routes
(def routes
  `[[["/" {:get home-page}
      ^:interceptors [(body-params/body-params) http/html-body]
      ["/about" {:get about-page}]
      ["/videos" {:get get-videos}]
      ["/videos/:video" {:get get-video}]]]])



;; Consumed by helove-service.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false}})

