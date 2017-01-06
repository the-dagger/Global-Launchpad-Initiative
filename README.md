What is it?
====
A simple Android App built on top of Firebase which can serve as a medium to share resources among users.<br>
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
- [x] Implementing a service to fetch data from firebase db and display a notification when a new data is added.
- [x] Implement the delete ability for admins (implemented partially).
- [ ] Add twitter login.
- [ ] Add facebook login.
- [ ] Create a preference screen to manage user profile and enable/disable the background refresh.
- [ ] Add ability to bookmark a particular resource.
- [x] Implement functionality to add admins via Firebase Remote Config.

Screenshots :
====
![](https://i.imgur.com/J9WKLCL.png?1)  ![](https://i.imgur.com/nwoscQb.png?1)  ![](https://i.imgur.com/krBQleU.png?1)

Compiling for your own use?
====
Create a project in firebase console, link this project to it and palce the `google-services.json` inside  [app/](/app/) directory of your project.

Project Maintainers :
====
[@the-dagger](https://github.com/the-dagger) and [@gogeta95](https://github.com/gogeta95)

Open Source Libraries Used :
====
* [Picasso](https://github.com/square/picasso)
* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [MaterialDialogs](https://github.com/afollestad/material-dialogs)
* [CircleImageView](https://github.com/hdodenhof/CircleImageView)
