(ns forge-mtg-tools.mtgtop8
  (:require [clj-http.client :as client]
            [forge-mtg-tools.converter :as converter]
            [hickory.core :refer [parse as-hickory]]
            [hickory.select :as s]))

(def mtgtop8-root "https://mtgtop8.com")

(defn get-event-htree
  "Given a url, get the hickory tree"
  [url]
  (-> (client/get url)
      :body
      parse
      as-hickory))

(defn get-mtgo-file-url
  "Given a URL, get the dec-file link"
  [url]
  (let [htree (get-event-htree url)]
    (-> (s/select (s/attr :href #(re-matches #"^dec.*$" %)) htree) first :attrs :href)))

(defn get-deck-urls
  "Given a hickory htree of an event (returned from get-event-html), get the urls of decks for that event"
  [htree]
  (let [events-url (str mtgtop8-root "/event")
        first-deck (map #(-> % :attrs :href)
                        (-> (s/select
                             (s/child (s/class "chosen_tr")
                                      (and (s/tag :div)
                                           (s/attr :class #(re-matches #"W.*" %)))
                                      (s/tag :a))
                             htree)))
        rest-decks (map #(-> % :attrs :href)
                        (-> (s/select
                             (s/child (s/class "hover_tr")
                                      (and (s/tag :div)
                                           (s/attr :class #(re-matches #"S.*" %)))
                                      (s/tag :a))
                             htree)))
        decks (-> (merge first-deck rest-decks) flatten)
        deck-urls (map #(str events-url %) decks)
        mtgo-file-urls (map #(str mtgtop8-root "/" (get-mtgo-file-url %)) deck-urls)]
    mtgo-file-urls))

(defn get-deck-file
  "Given a url, retrieve the deck resposne"
  [url]
  (let [resp (client/get url)
        content (:body resp)
        filename (->> (-> resp :headers (get "Content-Disposition")) (re-find #"filename=(.*)") second)]
    {:content content
     :filename filename}))

(defn download-files
  [event-url]
  (let [htree (get-event-htree event-url)
        event-title (-> (s/select (s/descendant
                                   (s/class "w_title")
                                   (s/tag :tr)
                                   (s/and (s/tag :td) (s/nth-child 1)))
                                  htree) first :content first
                        (clojure.string/replace #" " "_"))
        event-date (clojure.string/replace
                    (->> (-> (s/select (s/find-in-text #"\d* players - \d\d\/\d\d\/\d\d") htree)
                             first :content)
                         (filter string?)
                         (map #(re-find #"\d* players - (\d\d\/\d\d\/\d\d)" %))
                         (filter (comp not nil?))
                         first second)
                    #"\/" "_")
        dir-name (str event-title "_" event-date)
        deck-urls (get-deck-urls htree)
        deck-files (map get-deck-file deck-urls)]
    (clojure.java.io/make-parents (str "resources/decks/"  dir-name "/" (-> deck-files first :filename)))
    (doall (map #(spit (str "resources/decks/" dir-name "/" (:filename %))
                       (:content %)) deck-files))
    (converter/mwdeck-dir->dck-dir (str "resources/decks/" dir-name))))

;; ex:
;; (download-files "https://www.mtgtop8.com/event?e=23638&d=363913")
