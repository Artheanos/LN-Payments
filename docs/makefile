ADOC_OPTS := --failure-level WARN -r asciidoctor-diagram
BUILD_DIR := ./target
SRC_DIR := ./src
DEPENDENCIES_DIR := /dependencies
SOURCES := $(shell ls $(SRC_DIR)/*.adoc 2>/dev/null)
TARGETS := $(SOURCES:$(SRC_DIR)/%.adoc=$(BUILD_DIR)/%)
DEPENDENCIES := $(shell ls $(SRC_DIR)$(DEPENDENCIES_DIR)/*.adoc 2>/dev/null)
DEPENDENCIES_TARGET := $(DEPENDENCIES:$(SRC_DIR)$(DEPENDENCIES_DIR)/%.adoc=$(BUILD_DIR)/%)

define COMPILE_PDF
    asciidoctor-pdf $(ADOC_OPTS) -o $(1) $(2)
endef

.PHONY: clean all pdf html dir dependencies book

all: html pdf book

pdf: $(TARGETS:=.pdf)

html: $(TARGETS:=.html)

dependencies: $(DEPENDENCIES_TARGET:=.pdf)

book: book.pdf

book.pdf: $(BUILD_DIR)/title-page.pdf $(BUILD_DIR)/kp.pdf $(BUILD_DIR)/praca-dyplomowa.pdf
	pdftk $? cat output $(BUILD_DIR)/book.pdf

$(BUILD_DIR)/%.pdf: $(SRC_DIR)/%.adoc dir dependencies
	$(call COMPILE_PDF,$@,$<)

$(BUILD_DIR)/%.html: $(SRC_DIR)/%.adoc dir dependencies
	asciidoctor -a data_uri $(ADOC_OPTS) -o $@ $<

$(BUILD_DIR)/%.pdf: $(SRC_DIR)$(DEPENDENCIES_DIR)/%.adoc
	$(call COMPILE_PDF,$@,$<)

dir:
	mkdir -p $(BUILD_DIR)

clean:
	rm -rf $(BUILD_DIR)
