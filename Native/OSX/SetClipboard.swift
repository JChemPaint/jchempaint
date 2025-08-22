import Cocoa
import Foundation

// force the function to have a name callable by the c code
@_silgen_name("setClipboard")
public func setClipboard(pdfData: UnsafePointer<CUnsignedChar>, pdfDataSize: Int,
                         svgData: UnsafePointer<CUnsignedChar>, svgDataSize: Int,
                         pngData: UnsafePointer<CUnsignedChar>, pngDataSize: Int,
                         str: UnsafePointer<CChar>) -> Void {
    
    
    let pb = NSPasteboard.general
    // Java will set the chemical/ mime types, we will just deal with the
    // native platform ones.
	//   pb.clearContents()
    
	// JWM: it should be possible to copy as SVG but I think we need to provide
	// and "SVG class", the mime type should be this, it doesn't matter though
	// since the PDF copy works well with Graphic tools/Pages/Office.
	if svgDataSize != 0 {
		let svgType = NSPasteboard.PasteboardType("public.svg-image")
		let data = Data(bytes: svgData, count: svgDataSize)
        pb.setData(data, forType: svgType)
	}

    if pdfDataSize != 0 {
        let data = Data(bytes: pdfData, count: pdfDataSize)
        pb.setData(data, forType: .pdf)
    }

    if pngDataSize != 0 {
        let data = Data(bytes: pngData, count: pngDataSize)
        pb.setData(data, forType: .png)
    }

    pb.setString(String(cString:str), forType: .string)
}
