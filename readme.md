## Food app

Food recipes app using https://spoonacular.com/ as endpoint

DONE:

* test insets and themes on old android versions
* add translucent controls
* clear git repo from misc data (# .idea files)

TODO:

* add edit button on favorites screen as side kick of long click gesture + add check mark on
  selected/unselected items
* change to overlays instead specifying color for child controls
    * some styles can be remade to be overlays
* add use cases
* refactor inter-layer communications for usage of interfaces instead of specific implementations
* check why splash screen looks different on api28 and api31

* add navigator
* move to state objects for UI instead of scattered data
* add registration
* change LiveData with StateFlow
* add rest of diets and food types
* refactor code to use RepeatOnLifecycle/flowOnLifeCycle instead of launchOn
* add onboarding
* add russian version
* add swipe to refresh
* test to rotation state saving
* check app with leak canary (e.g. clearance of data bindings with live data (possible leak of
  viewLifeCycleOwner as we don't set it and believe in byViewBinding) ) + profiler
* move logic from fragments and activities
* separate app to modules
* add unit tests
* refactor saving recipes request data (not json file of whole bunch on every request)
* add pagination
* refactor jsoup usage to correctly show internet tags in recipe description and not ignore them
* refactor repository to consume models, not entities for saving and also return models, not
  entities
* refactor favorites logic - to keep only id, as data in recipe can change and we can keep outdated
  recipe in favorites
* decide whenever use or not use data binding in project and refactor for same style in all
  fragments
* fix bug with opening bottom sheet on freshly installed app
* add spinner while opening web page in recipes