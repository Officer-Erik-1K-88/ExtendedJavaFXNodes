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

## TutorialWindow
Information to be added at a later date.

## TutorialScene
Information to be added at a later date.

## TutorialContent
Information to be added at a later date.

# GAXML
While what is in this library isn't actually what GAXML truly is, it is only here to
give taste to its wonderful functionality.

## Theme
The Theme class is there to provide theming for the [XMLProcessor](#xmlprocessor) to apply to the page.

## XMLProcessor
The [XMLProcessor](src/main/java/com/airent/extendedjavafxnodes/gaxml/XMLProcessor.java) is the meat of the entire GAXML formatting,
as this class does all the processing that is necessary for GAXML to run and operate.

## JavaScript
GAXML does have JavaScript handling for those necessary inline functions in GAXML.

## Tags

### page
The top level element
can also be used for defining reusable templates
that can be called for by a call of an inline page tag.
All inline page tags must have a 'path' attribute that represents
the directory to the GA-XML file.
The path must be separated using '/',
for example, the path must be like "path/to/file.xml."

Inline pages are defined as a page tag that has the 'type' attribute
set as either "inline" or "inline-notice."
Normal inline has no identifying markings that separate the inline page
from the rest of the page.
While, inline-notice makes it so that there are identifying marks.
To add a defined marking (as a text statement),
define the 'notice' attribute, otherwise the defined marking is the path.

### br
A line break can be used to add vertical distance between elements.
Can change space by changing the 'size' attribute.

### hr
A horizontal line can be used to define separation between content.
The line created can be changed in width by setting the 'size' attribute,
by default, the size is 4, and the height is 40. The height is set with the
'height' attribute, and is the largest the size is allowed to be.
Height is also the amount of space that the hr tag takes up.
Any space not taken up by the actual line is roughly distributed between the
top and bottom of the line, making the line to be centered in the extra space.

### p
The paragraph tag, this tag's main purpose is to state large blocks of
content that should be grouped and kept separate from the content out
side of the tag.

### span
The span tag, like the paragraph tag, is used to group content that
is separate from the rest of the content.
However, it is similar to the surrounding content where
the tag's content should still remain inline with
the content that is around it.
Meaning that the span tag is to separate
content to format.

### pre
The pre-tag is the exact same as the paragraph tag, but doesn't
remove excess space.

### variable
The variable tag defines a value that can be called by
a var tag.

### var
The var tag is the caller of the content in a variable tag.

### math
The math tag defines an equation that needs to be calculated
displaying the answer to the equation.

### script
The script tag defines JavaScript that can be processed and used
to draw out event calls, and functions defined in a script tag
can also be called by var and variable tags given "script." is
provided before the function execution call in the var or variable tag.

### function
This tag is the same as the [script tag](#script),
however, this one is only for the use of a single JavaScript function.
