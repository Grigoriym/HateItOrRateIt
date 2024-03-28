[![codecov](https://codecov.io/gh/Grigoriym/HateItOrRateIt/graph/badge.svg?token=EPFJKZ1EJ7)](https://codecov.io/gh/Grigoriym/HateItOrRateIt) [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/94b85590c7744537b0219e444b073a12)](https://app.codacy.com/gh/Grigoriym/HateItOrRateIt/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

## HateItOrRateIt

This application offers a straightforward way to keep track of products you like
or dislike. It allows you to easily upload photos, names, descriptions, and store
information, enabling you to make informed decisions about future purchases.

|  <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/049be3a0-86b8-4d62-b798-e40efafd7f0d" alt="drawing" width="400" height="800"/> | <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/69d6a136-459e-4fdf-b368-a0829d846e38" alt="drawing" width="400" height="800"/>  |
|---|---|
| <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/593edc54-f1fb-468a-a574-e972182640ac" alt="drawing" width="400" height="800"/>  |  <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/cf787dc6-89ff-4c6d-ac5c-830819eebb5d" alt="drawing" width="400" height="800"/> |
| <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/d74bf10f-a1f3-4200-89ec-8d4616e40052" alt="drawing" width="400" height="800"/>  | <img src="https://github.com/Grigoriym/HateItOrRateIt/assets/31949421/5e34b4f2-1a6c-4bd2-95cf-6535ecd271a6" alt="drawing" width="400" height="800"/>  |


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
