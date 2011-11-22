SWADroid
================================

Android client for e-learning platform SWAD.


Content
-------

SWADroid/         	Android client.
Webservices/            Webservices implemented in SWAD server.
COPYING			License file.
HISTORY.md		Changelog file.
README.md		This file.


Installation
-----------

    gem install github-markup


Usage
-----

    require 'github/markup'
    GitHub::Markup.render('README.markdown', "* One\n* Two")

Or, more realistically:

    require 'github/markup'
    GitHub::Markup.render(file, File.read(file))


Testing
-------

To run the tests:

    $ rake

To add tests see the `Commands` section earlier in this
README.


Contributing
------------

1. Fork it.
2. Create a branch (`git checkout -b my_markup`)
3. Commit your changes (`git commit -am "Added Snarkdown"`)
4. Push to the branch (`git push origin my_markup`)
5. Create an [Issue][1] with a link to your branch
6. Enjoy a refreshing Diet Coke and wait


License
--------

SWADroid client is published under GPLv3 license.
Webservices are published ander AGPLv3 license.


Contact
--------

Juan Miguel Boyero Corral

### Email

juanmi1982@gmail.com

###Twitter

@louisverona


[r2h]: http://github.com/github/markup/tree/master/lib/github/commands/rest2html
[r2hc]: http://github.com/github/markup/tree/master/lib/github/markups.rb#L13
[1]: http://github.com/github/markup/issues
