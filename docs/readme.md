### Documents

## How to set up env

First you need to install Ruby, and set PATH variable to be able to execute commands installed by gem.
After that install these packets:
```
gem install asciidoctor
gem install asciidoctor-pdf
gem install asciidoctor-diagram
gem install rouge
```
You will also need _make_ build tool and a program called _pdftk_. It can be found in any linux package manager repo.

## Building

Makefile can generate PDF and HTML files. They will appear in _target_ directory. Simply type *make pdf*, *make html*. 
By default it will generate both formats. There is also clean task to delete _target_ directory.

### Thesis book

You can build a book with command *make book*. Some parts of the book are linked
at compile time, that is why you don't see them in src files (like title page and disclaimer).