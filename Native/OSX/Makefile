
.PHONY: all

all: libSetClipboard.dylib

clean:
	rm -rd *.a *.dylib *.o

libSetClipboardSwift_x86_64.a: SetClipboard.swift Makefile
	swiftc SetClipboard.swift -target x86_64-apple-macosx15 -static -emit-library -o libSetClipboardSwift_x86_64.a

libSetClipboardSwift_arm64.a: SetClipboard.swift Makefile
	swiftc SetClipboard.swift -target arm64-apple-macosx15 -static -emit-library -o libSetClipboardSwift_arm64.a

libSetClipboard_x86_64.o: libSetClipboardSwift_x86_64.a setclipboard.c
	gcc -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/darwin/" \
	    -arch x86_64 -dynamiclib -o libSetClipboard_x86_64.o setclipboard.c libSetClipboardSwift_x86_64.a

libSetClipboard_arm64.o: libSetClipboardSwift_arm64.a setclipboard.c
	gcc -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/darwin/" \
	    -arch arm64 -dynamiclib -o libSetClipboard_arm64.o setclipboard.c libSetClipboardSwift_arm64.a

libSetClipboard.dylib: libSetClipboard_x86_64.o libSetClipboard_arm64.o
	lipo -create libSetClipboard_x86_64.o libSetClipboard_arm64.o -output libSetClipboard.dylib
