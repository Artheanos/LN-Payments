### Documents

## How to set up env

First you need to install Ruby, and set PATH variable to be able to execute commands installed by gem.
After that install these packets:
```
gem install asciidoctor
gem install asciidoctor-pdf
gem install asciidoctor-diagram
```

## Building

Makefile can generate PDF and HTML files. They will appear in _target_ directory. Simply type *make pdf*, *make html*. 
By default it will generate both formats. There is also clean task to delete _target_ directory.
