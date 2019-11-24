(ns forge-mtg-tools.core)

(defn read-deck
  "Read an MTGO deck and return a vector of cards"
  [r]
  (let [cards (-> (slurp r)
                  (clojure.string/split #"\n")
                  (->> (mapv #(clojure.string/replace % #"\r" ""))))
        main (doall (take-while #(not= % "Sideboard") cards))
        sideboard (->> (clojure.set/difference (set cards)
                                               (set main))
                       (filter #(not= % "Sideboard")))]
    {:main main
     :sideboard sideboard}))

(defn card-repeat
  "Given a string <n> <cardname> return a vector of n [cardname]"
  [s]
  (let [[_ card-count card-name] (re-matches #"^(\d) ([\w\W]*)$" s)]
    (into []
          (repeatedly (clojure.edn/read-string card-count)
                      (constantly card-name)))))

(defn cards-vector
  "Given a list of card-count and card-names, return a card vector"
  [coll]
  (->> coll
       (mapv card-repeat)
       flatten
       (into [])))

(defn forge-list
  "Given a coll, join with ;"
  [coll]
  (clojure.string/join ";" coll))

;; https://stackoverflow.com/questions/7662447/what-is-idiomatic-clojure-to-remove-a-single-instance-from-many-in-a-list
(defn remove-card
  "Remove exactly one instance of card name s from cards-vector v"
  [s v]
  (let [[n m] (split-with (partial not= s) v)] (concat n (rest m))))

(defn remove-cards
  "Remove the cards in vector v from deck coll"
  [v coll]
  (if-not (seq v)
    coll
    (remove-cards (rest v) (remove-card (first v) coll))))

;; get a Forge list for main deck
(-> #_(read-deck "/Users/james/Downloads/Legacy_Canadian_Threshold_by_GodOfSlaughter.txt")
    (read-deck "/Users/james/Downloads/Legacy_Storm_by_CyrusCG.txt")
    :main
    cards-vector
    ((partial remove-cards ["Cabal Ritual"
                            "Dark Ritual"
                            "Lotus Petal"
                            "Cabal Ritual"
                            "Thoughtseize"
                            "Lion's Eye Diamond"
                            "Infernal Tutor"
                            "Underground Sea"]))
    forge-list)
