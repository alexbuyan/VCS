# VCS
Implemented VCS and CLI with support for basic commands:

* `init` -- initialize repository
* `add <files>` -- add files to repository
* `rm <files>` -- delete files from repository
* `status` -- show current status of the repository
* `commit <message>` -- create a commit from repository
* `reset <to_revision>` -- works like `git reset --hard`
* `log [from_revision]`
* `checkout <revision>`
    *  Possible values of revision `revision`:
        * `commit hash`
        * `master` -- reset branch to initial state
        * `HEAD~N`, where `N` -- non-negative integer. `HEAD~N` is Nth commit before HEAD (`HEAD~0 == HEAD`)
* `checkout -- <files>` -- reset changes in files
