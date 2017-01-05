Global-Launchpad-Initiative
====
Android App for Global Launchpad Initiative

What is it?
====
A simple Android App built on top of Firebase which can server as a medium to share resources among users.<br>
The resources can be further broken down in 4 categories :
* Educational Resources
* Hackathons
* Meetups
* Technical talks

As on now, only the admins (defined in an Arraylist) inside `MainActivity.java` are allowed to add content, so if you want to make youself an Admin, feel free to send a Pull Request in and you'll be added up.

##You can download the latest release from 
[https://github.com/the-dagger/Global-Launchpad/releases](https://github.com/the-dagger/Global-Launchpad/releases)

Features : 
====
* Supports user login
* Add a new resource (admins only)
* View existing resources
* Share the app among other users, implemented using Firebase Deep Links and App Invites.

TODO : 
====
- [ ] Implementing a service to fetch data from firebase db and display a notification when a new data is added.
- [ ] Create a preference screen to manage user profile and enable/disable the background refresh.
- [ ] Implement the delete ability for admins (implemented partially).
- [ ] Add ability to bookmark a particular resource.

Compiling for your own use?
====
Create a project in firebase console, link this project to it and palce the `google-services.json` inside  [app/](/app/) directory of your project.


Screenshots :
====
![](https://i.imgur.com/J9WKLCL.png?1) ![](https://i.imgur.com/nwoscQb.png?1)
