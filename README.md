[![codecov](https://codecov.io/gh/Grigoriym/HateItOrRateIt/graph/badge.svg?token=EPFJKZ1EJ7)](https://codecov.io/gh/Grigoriym/HateItOrRateIt) [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/94b85590c7744537b0219e444b073a12)](https://app.codacy.com/gh/Grigoriym/HateItOrRateIt/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

## HateItOrRateIt

This application offers a straightforward way to keep track of products you like
or dislike. It allows you to easily upload photos, names, descriptions, and store
information, enabling you to make informed decisions about future purchases.

## This app offers

1. Day/Night theme
2. On/off crashlytics

## TODOs

1. Create some sort of synchronization
2. Make the possibility to change the view type in the List (small tiles, big
   product tile)
3. Kotest?
4. Privacy Policy
5. Day night theme selector
6. Languages selector
7. Stubs for debug analytics

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

## Definitions

1. **Original folder**: the folder that is created upon creating the Product with
   images, e.g., `files/products/1_2024-01-18_15-49-21`
2. **Backup folder**: the folder where the initial state of images is saved to be
   accessed if we remove original images and want to revert everything
   `files/products/1_2024-01-18_15-49-21_backup`
3. **Temp folder**: the folder where we store newly added images in the product
   being edited, so that we can remove them easily in case of reverting.
   e.g., `files/products/1_2024-01-18_15-49-21_temp`
