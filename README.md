# Lorrem

A single page application frontend for printing nonsense into a jira-like HTML view for fun. Made with clojure.

# How to develop

The devcontainer (if you use VSCode) has all the dependencies and does `npm install` automatically.

After startup, you can run `npx shadow-cljs watch lorrem`to create a build and start a dev server in http://127.0.0.1:8080/. Currently images are not loaded by default, so that I don't need to store them into git. They are added in my production server as a docker volume. 

You may need to remove .shadow-cljs folder to get the build working, at least if it gives error `shadow-cljs - socket connect failed, server process dead?`.