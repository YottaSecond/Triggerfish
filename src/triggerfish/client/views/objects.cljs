(ns triggerfish.client.views.objects
  (:require
   [reagent.core :refer [atom create-class dom-node]]
   [triggerfish.client.utils.hammer :refer [hammer-manager add-tap add-pan add-pinch]]
   [re-frame.core :refer [subscribe dispatch]])
  (:require-macros
   [triggerfish.client.utils.macros :refer [deftouchable]]))

(def obj-retracted-style
  {:min-height "40px"
   :min-width  "70px"})

(def obj-expanded-style
  {
   })

(defn inlet [obj-id [inlet-name inlet-params]]
  [:div {:class "inlet"} inlet-name])

(defn outlet [obj-id [outlet-name outlet-params]]
  (let [selected-outlet (subscribe [:selected-outlet])
        selected? (= [obj-id outlet-name] @selected-outlet)]
    [:div {:class (if selected? "outlet selected-outlet" "outlet")
           :on-click (fn [e] (dispatch [:select-outlet obj-id outlet-name]))}
     outlet-name]))

(defn obj-display [{:keys [id outlets inlets]} as params]
  [:div {:class "object-display"}
   [:div {:class "io-container"}
    (map (fn [in]
           ^{:key (str id "inlet:" (first in))}
           [inlet id in])
         inlets)]
   [:div {:class "io-container"}
    (map (fn [out]
           ^{:key (str id "outlet:" (first out))}
           [outlet id out])
         outlets)]])

(deftouchable object-impl [id params expanded]
  (add-pan ham-man
           (fn [ev]
             (.stopPropagation (.-srcEvent ev)) ;; without this, you can pan the camera while moving an object by using two fingers.
             (let [delta-x (.-deltaX ev)
                   delta-y (.-deltaY ev)]
               (if-not (.-isFinal ev)
                 (dispatch [:offset-object id delta-x delta-y])
                 (dispatch [:commit-object-position id])))))
  (add-pinch ham-man
             (fn [ev]
               (.stopPropagation (.-srcEvent ev)))
             (fn [ev]
               (.stopPropagation (.-srcEvent ev))))
  (fn [id]
    (let [params @params
          {:keys [x-pos y-pos offset-x offset-y name]} params]
      [:div {:class            "object"
             :style (merge (if true #_@expanded
                             obj-expanded-style
                             obj-retracted-style)
                           {:position         "fixed"
                            :left             x-pos
                            :top              y-pos
                            :transform        (str "translate3d(" offset-x "px, " offset-y "px, 0px)")
                            :transition       "min-width 0.25s, min-height 0.25s"})
             :on-click (fn [e] (.stopPropagation e) (swap! expanded not))}
       [:p name]
       (when true #_expanded
         [obj-display params])])))

(defn object [id]
  (let [params (subscribe [:obj-params id])
        expanded (atom false)]
    [object-impl id params expanded]))