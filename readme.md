## Food app

Food recipes app using https://spoonacular.com/ as endpoint

TODO:
* clear git repo from misc data (# .idea files) 
* add navigator
* add registration
* add rest of diets and food types
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
* refactor overview fragment so user can scroll not only recipe description but whole page
* refactor repository to consume models, not entities for saving and also return models, not
  entities
* refactor favorites logic - to keep only id, as data in recipe can change and we can keep outdated
  recipe in favorites
* decide whenever use or not use data binding in project and refactor for same style in all
  fragments
* fix bug with opening bottom sheet on freshly installed app
* add spinner while opening web page in recipes