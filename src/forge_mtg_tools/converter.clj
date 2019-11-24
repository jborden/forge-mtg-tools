(ns forge-mtg-tools.converter
  (:require [instaparse.core :as insta :refer [defparser]]
            [instaparse.transform :refer [transform]]
            [me.raynes.fs :as fs]))

(defparser mwDeck
  (slurp "src/bnf/mwDeck.bnf"))

(defn transform-cards
  [parse-tree]
  (let [card-converter (fn [type count set cardname]
                         (hash-map
                          (first count)
                          (second count)
                          (first set)
                          (second set)
                          (first cardname)
                          (second cardname)
                          :type type))]
    (transform {:MAINCARD (partial card-converter "mainboard")
                :SBCARD (partial card-converter "sideboard")}
               parse-tree)))

(defn parse-mwdeck
  [f]
  (->> (slurp f)
       mwDeck
       transform-cards
       (filter map?)
       (group-by :type)))

(defn mwdeck->dck
  "Convert a file into Forge dec string"
  [f]
  (let [filename (-> (clojure.java.io/file f)
                     .toPath
                     .getFileName
                     .toString)
        parsed-deck (parse-mwdeck f)
        mainboard (get parsed-deck "mainboard")
        sideboard (get parsed-deck "sideboard")
        card->str (fn [m] (str (:COUNT m)
                               " "
                               (clojure.string/replace (:CARDNAME m) #"\/" "//")
                               (when-not (clojure.string/blank? (:SET m))
                                 (str "|" (:SET m)))))
        mainboard-card-s (map card->str mainboard)
        sideboard-card-s (map card->str sideboard)]
    (str "[metadata]\n"
         "NAME=" filename "\n"
         ;; this might not be needed (?)
         "[Avatar]\n\n"
         "[Main]\n"
         (doall (apply str (map #(str % "\n") mainboard-card-s)))
         "[Sideboard]\n"
         (doall (apply str (map #(str % "\n") sideboard-card-s)))
         ;; this might not be needed (?)
         "[Planes]\n\n[Schemes]\n\n[Conspiracy]\n\n")))

(defn mwdeck-dir->dck-dir
  "Given a dir, convert all .mwdeck files to .dck files"
  [dir]
  (let [mwdeck-files (->> (fs/find-files dir #".*mwDeck"))
        parent (-> (fs/parent (first mwdeck-files)) fs/parent)
        dir-base-name (fs/base-name dir)
        new-dir (str parent "/" dir-base-name "_dck_files")]
    (fs/mkdir new-dir)
    (doall (map #(let [[name ext] (fs/split-ext %)
                       path (.getParent %)
                       parsed-deck (mwdeck->dck %)
                       new-filename (str new-dir "/" name ".dck")]
                   (spit new-filename parsed-deck))
                mwdeck-files))))

;; (mwdeck-dir->dck-dir "resources/The_15th_God_of_Legacy_@_Hareruya_(Japan)_09_11_19")
;; (mwdeck-dir->dck-dir "resources/MTGO_Legacy_PTQ_10_11_19")
;;(spit "test_deck.dck" (mwdeck->dck "resources/The_15th_God_of_Legacy_@_Hareruya_(Japan)_09_11_19/test_case.mwDeck"))

;; (-> (slurp "resources/The_15th_God_of_Legacy_@_Hareruya_(Japan)_09_11_19/Legacy_Painter_by_Oda_Kouichi.mwDeck") mwDeck)

