What is it?
====
The app is built on the idea that opportunities are everywhere, but people are not aware about them.<br>
This is a simple Android App built on top of Firebase which can serve as a medium for sharing resources among its users.<br>
The resources can be further broken down in 4 categories :
* Educational Resources
* Hackathons
* Meetups
* Technical talks

As on now, only the admins are allowed to add/remove content, so if you want to make youself an Admin, feel free to send a Pull Request in with your email address with which you signed up in the app and you'll be added up.

##You can download the latest release from 
[https://github.com/the-dagger/Global-Launchpad/releases](https://github.com/the-dagger/Global-Launchpad/releases)

##Note : 
There are 2 releases, in the demo release, marked as `demo`, anyone is able to add/delete resources where are in the prod release, marked as `prod`, only the admins are able to add/delete resources.

Features : 
====
* Supports user login
* Add a new resource / Delete existing ones (admins only)
* View/Share existing resources
* Notifications for addition of new resources
* Share the app among other users, implemented using Firebase Deep Links and App Invites

TODO : 
====
- [ ] Add twitter login.
- [ ] Add facebook login.
- [ ] Create a preference screen to manage user profile and enable/disable the background refresh.
- [ ] Add ability to bookmark a particular resource.

Screenshots :
====
![](https://i.imgur.com/J9WKLCL.png?1)  ![](https://i.imgur.com/nwoscQb.png?1)  ![](https://i.imgur.com/krBQleU.png?1)

Compiling for your own use?
====
Create a project in firebase console, link this project to it and palce the `google-services.json` inside  [app/demo](/app/demo) and [app/prod](/app/prod) directory of the project.

Want to contribute to the project?
====
Contributors to the project are most welcomed you can contribute to the project in following ways,
* Take up any of the existing issues 
* Report a new issue (Feature Request / Bug Report)

Project Maintainers :
====
[@the-dagger](https://github.com/the-dagger) and [@gogeta95](https://github.com/gogeta95)

Open Source Libraries Used :
====
* [Picasso](https://github.com/square/picasso)
* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [MaterialDialogs](https://github.com/afollestad/material-dialogs)
* [CircleImageView](https://github.com/hdodenhof/CircleImageView)
