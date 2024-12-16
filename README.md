# About
This project will add a few new nodes into JavaFX and make some JavaFX nodes even better.

# The Available Nodes

## FilePicker

The FilePicker Node creates a file/directory picker on a page. Though this class uses JavaFX's FileChooser and DirectoryChooser classes for the actual file/directory picking, it is designed to facilitate access to and use those classes.

## Dialog

The Dialog class is just my recreation of JavaFX's Dialog class. Theoretically, they should be almost identical, with a few minor changes. I created this class to control the creation of my Alert class better.

## Alert

I've created this class to be like JavaFX's Alert class, with a few changes. I have revamped the images by a small margin to give them a slightly fresher look (I am still working on getting the photos better integrated). My Alert class has 7 AlertTypes, meaning that I've added only two new AlertTypes from JavaFX's base 5 AlertTypes. So, I will not describe the five in JavaFX, as they still do the same things in my Alert. The two new AlertTypes are "prompt" and "validate".

The "prompt," like its name, gives the user a text prompt to fill in. This lets you get user input without revamping and adding more content to pages.

The "validate" AlertType is precisely the same as the "confirmation" AlertType but with a slightly different image icon. This AlertType is meant mainly for validation calls when you want the user to validate an action to be performed.
