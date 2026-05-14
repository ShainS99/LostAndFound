# Lost and Found App

A simple Android app built in Android Studio.
The app allows users to create, view, and manage lost and found item advertisements.

Users can:
* Create a new advert for a lost or found item
* View all posted items
* Filter items by category
* View item details
* Remove items
New:
* Attach a location to an advert
* View nearby adverts on a map

## Features
### Create Advert
* Enter item name, description, phone number and location
* Select a category and type (Lost or Found)
* Add an image from the device
* Input a date for when it occured
* Automatically stores a timestamp when the advert is created
* Attach an address for where it occured

### View Items
* Displays all items using a RecyclerView
* Items are sorted by most recent
* Filter items by category using a dropdown

### Item Details
* View full information about a selected item
* Display image and contact details
* Option to delete the item

### Map
* View a map of adverts
* Adjust search based on distance

## Database
The app uses SQLite for persistent storage.

Table: items
* id (INTEGER PRIMARY KEY)
* type (TEXT)
* name (TEXT)
* phone (TEXT)
* description (TEXT)
* category (TEXT)
* imagePath (TEXT)
* date (TEXT)
* createdAt (TEXT)
* location (TEXT)
* latitude (REAL)
* longitude (REAL)

## Author
Shain
