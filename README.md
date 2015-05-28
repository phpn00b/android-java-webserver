# Simple Android Web Server aka FoxyServer
![foxy logo](https://raw.githubusercontent.com/phpn00b/android-java-webserver/master/foxylogo.png)
## Overview
My company builds applications that run on embedded devices running android. We had a use case that required these devices to be able to serve up an application to end users (not likely more than 10 at a time). I found plenty of full featured solutions like [spring](https://spring.io/) or [restlet](http://restlet.com/) which is what I was already using for a headless data api.
Sadly I didn't fully appreciate the value of [NanoHttpd](https://github.com/NanoHttpd/nanohttpd) till I had already basically written a knock off of it. However make no mistake this does several things that HanoHttpd does not. Like authentication and authorization as well as offering a more robust out of box set of tools to build a CRUD style application.

These frameworks to while offering a massive selection of features and options seemded to not be as straight forward as I was hoping and while I could get them up and going in minutes I found that if I wanted to just make a change or override something that it does by default I practically would hit a brick wall or spend hours digging around in the source to find out how to do what I wanted. 

Both of these frameworks were designed to run on real (read as not a phone/tablet/other low power android device) servers with full Java standing behind it and were later ported to being able to run on android and at times it is all to clear that is the case.

## foxy server
Written from the start to be simple with very few dependencies. Very straight forward in what it does. Free for all. 

### Supported features
* GET and POST verbs (other can easily be added)
* Custom Logic to handle requests
* Routing requests to either custom logic or to static pages
* Authentication
* Permissions
* Users and Roles that you define and be controlled via the running server
* Simple easy to work with MVC framework.
* Ability to embed static resources in your apk or run them off the file system of the device
 * Also comes with a developing mode where you can edit your html/css/js and run the simple push script which will copy to device almost instantly

Feel free to reach out if you have questions. 
