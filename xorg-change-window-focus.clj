#!/usr/bin/env bb
(ns xorg-change-window-focus
  (:require
   [babashka.process :refer [shell process exec]]
   [clojure.string :as str]
   )
  )

(defn xwininfo [id]
  (into {} (remove nil?
                   (for [line (-> (shell {:out :string} (str "xwininfo -id " id))
                                  :out
                                  str/split-lines
                                  )]
                     (let [index_of_colon (str/index-of line ":")] 
                       (cond 
                         (str/starts-with? line "xwininfo: Window id:") {:title (str/replace (subs line 32) "\"" "")}
                         (not (nil? index_of_colon)) (hash-map (str/trim (subs line 0 index_of_colon)) (str/trim (subs line (+ 1 index_of_colon))))
                         ))
                     )))
  )

(defn get-active-window []
  (str "0x" (str/replace (format "%8x" (Integer/parseInt (str/trim (:out (shell {:out :string} (str "xdotool getwindowfocus")))))) #"\s" "0"))
  )


(defn windows []
  (map (fn [s] (merge {:win_id (subs s 0 10) :desktop (Integer/parseInt (str/trim (subs s 11 13))) :title (subs s 14)} (xwininfo (subs s 0 10))))
       (-> (shell {:out :string} "wmctrl -l")
           :out
           str/split-lines
           ))
  )

(defn windows-of-desktop [desktop & [windows-list]] (filter #(= desktop (:desktop %)) (or windows-list (windows))))
(defn which-desktop? [win_id & [windows-list]] (:desktop (first (filter #(str/ends-with? (:win_id %) win_id) (or windows-list (windows))))))
(defn focus-window! [win_id] (shell (str "xdotool windowraise " win_id)))


(defn compare-x-position [cmp_func, el1, el2]
  (cmp_func (Integer/parseInt (get el1 "Absolute upper-left X")) (Integer/parseInt (get el2 "Absolute upper-left X")))
  )



(defn help []
  (println (str "-h show this help\n"
                "left - focus left window"
                "right - focus right window"
                ))
  )


(defn get-next [coll item]
  (->> (concat coll [(first coll)])
       (drop-while (partial not= item))
       second))

(defn raise-window! [direction] 
  (let [
        all_windows (windows)
        active_window_id (get-active-window)
        active_desktop (which-desktop? active_window_id)
        windows_on_current_desktop (windows-of-desktop active_desktop all_windows)
        compare_direction (partial compare-x-position (if (= direction "right") < >))
        sorted_windows (into [] (sort compare_direction windows_on_current_desktop))
        sorted_window_ids (map #(:win_id %) sorted_windows)
        next_window_id (get-next (cycle sorted_window_ids) active_window_id)
        ]
    (focus-window! next_window_id)
    (println "=======================")
    (println "Active xorg window:" active_window_id)
    (println "Next window id:" next_window_id)
    (for [win sorted_windows]
      (println (:win_id win) (get win "Absolute upper-left X") (:title win))
      )
    )
  )

(comment
  (raise-window! "right")
  (raise-window! "left")
  (get-active-window)
  )


(defn -main [& args]
  (case (first (seq args))
    "left" (raise-window! "left")
    "right" (raise-window! "right")
    (help)
    )
  )

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
