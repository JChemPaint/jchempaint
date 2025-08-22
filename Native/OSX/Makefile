
.PHONY: all

all: setclipboard

libSetClipboardSwift.dylib: SetClipboard.swift
	swiftc SetClipboard.swift -emit-library -static -o libSetClipboardSwift.a

setclipboard: libSetClipboardSwift.dylib setclipboard.c
	gcc -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/darwin/" \
            -o libSetClipboard.dylib -dynamiclib setclipboard.c libSetClipboardSwift.a

