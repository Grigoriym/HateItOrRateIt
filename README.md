[![codecov](https://codecov.io/gh/Grigoriym/HateItOrRateIt/graph/badge.svg?token=EPFJKZ1EJ7)](https://codecov.io/gh/Grigoriym/HateItOrRateIt) [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/94b85590c7744537b0219e444b073a12)](https://app.codacy.com/gh/Grigoriym/HateItOrRateIt/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

## HateItOrRateIt

This application offers a straightforward way to keep track of products you like
or dislike. It allows you to easily upload photos, names, descriptions, and store
information, enabling you to make informed decisions about future purchases.

## Why This App Can Be Helpful

There are times when you purchase a product that you either like or dislike, only to realize later that you can't remember its name. This is particularly true for items like cheese, ham, etc., where there are countless variations and it's challenging to keep up with all the names. This app aims to solve that problem, enabling you to recall the exact product the next time you need it.

https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/de24f8f3-ed42-4944-af75-548de1988259


|  <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/d745e722-9e59-4a81-a5ba-bdb1b614ab02" alt="drawing" width="400" height="900"/> | <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/902d61c4-ab54-43dc-a51c-1d8b87a6ac56" alt="drawing" width="400" height="900"/>  |
|---|---|
| <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/3e79ad35-7b87-43d0-b7fe-8adb5bcdd212" alt="drawing" width="400" height="900"/>  |  <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/ec249ed8-fa86-4aac-9c72-70bdc9e862f5" alt="drawing" width="400" height="900"/> |
| <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/c8acad80-5211-4d1f-94ff-2fba5bfdff1e" alt="drawing" width="400" height="900"/>  | <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/3a76cb2c-1a87-4492-9d4f-930efe0d840e" alt="drawing" width="400" height="900"/>  |


## This app offers

1. Day/Night theme
2. Language change: en, de, fr
3. On/off crashlytics, analytics

## TODOs

1. Create some sort of synchronization
2. Make the possibility to change the view type in the List (small tiles, big
   product tile)
3. ~~Kotest?~~ Jacoco is just fine
4. ~~Privacy Policy~~
5. ~~Day night theme selector~~
6. ~~Languages selector~~
7. ~~Stubs for debug analytics~~
8. ~~Check for app updates~~
9. StrictMode
10. Add version in settings

## Learnings

1. No need to pass data to viewModel by hand. SavedStateHandle can do all the work
2. Android Navigation does not support nullable Long values. More info
   [here](https://developer.android.com/guide/navigation/use-graph/pass-data#supported_argument_types)

## Bugs

1. ~~Being in details, fast double click on back button -> empty screen~~ | Solved
   by using safeClick extensions function
2. ~~In the screen where we add a product, add couple of images, swipe to the first
   image, change configuration, we are moved to the last image~~ | Fixed by using
   firstRecomposition variable.
3. What happens with the page indicator in Pager if we add many images?
4. In add/details screen, click any textField, close keyboard, add image from
   gallery -> why the keyboard is shown?
5. ~~Bottom Nav Bar has some (8.dp) line at the top~~
6. DarkMode Preferences click area
