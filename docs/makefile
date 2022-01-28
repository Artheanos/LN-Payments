ADOC_OPTS := --failure-level WARN -r asciidoctor-diagram
BUILD_DIR := ./target
SRC_DIR:= ./src
SOURCES := $(shell ls $(SRC_DIR)/*.adoc 2>/dev/null)
TARGETS := $(SOURCES:$(SRC_DIR)/%.adoc=$(BUILD_DIR)/%)

.PHONY: clean all pdf html dir

all: html pdf

pdf: $(TARGETS:=.pdf)

html: $(TARGETS:=.html)

$(BUILD_DIR)/%.pdf: $(SRC_DIR)/%.adoc dir
	asciidoctor-pdf $(ADOC_OPTS) -o $@ $<

$(BUILD_DIR)/%.html: $(SRC_DIR)/%.adoc dir
	asciidoctor -a data_uri $(ADOC_OPTS) -o $@ $<

dir:
	mkdir -p $(BUILD_DIR)

clean:
	rm -rf $(BUILD_DIR)


