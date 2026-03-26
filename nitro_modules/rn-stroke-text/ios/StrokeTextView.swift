import UIKit
import NitroModules

class StrokeTextView: UIView {

    private let label = StrokedTextLabel()
    private var fontCache: [String: UIFont] = [:]

    override init(frame: CGRect) {
        super.init(frame: frame)

        label.translatesAutoresizingMaskIntoConstraints = false
        addSubview(label)

        NSLayoutConstraint.activate([
            label.centerXAnchor.constraint(equalTo: centerXAnchor),
            label.centerYAnchor.constraint(equalTo: centerYAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    // MARK: - Layout

    override var intrinsicContentSize: CGSize {
        return label.intrinsicContentSize
    }

    // MARK: - Props

    @objc var text: String = "" {
        didSet {
            label.text = text
            invalidateIntrinsicContentSize()
        }
    }

    @objc var fontSize: NSNumber = 14 {
        didSet {
            updateFont()
        }
    }

    @objc var fontFamily: String = "Helvetica" {
        didSet {
            updateFont()
        }
    }

    @objc var color: String = "#000000" {
        didSet {
            label.textColor = colorStringToUIColor(color)
        }
    }

    @objc var strokeColor: String = "#FFFFFF" {
        didSet {
            label.outlineColor = colorStringToUIColor(strokeColor)
        }
    }

    @objc var strokeWidth: NSNumber = 0 {
        didSet {
            label.outlineWidth = CGFloat(truncating: strokeWidth)
        }
    }

    @objc var align: String = "center" {
        didSet {
            label.align =
                align == "left" ? .left :
                align == "right" ? .right : .center
        }
    }

    @objc var ellipsis: Bool = false {
        didSet {
            label.ellipsis = ellipsis
            invalidateIntrinsicContentSize()
        }
    }

    @objc var numberOfLines: NSNumber = 0 {
        didSet {
            label.numberOfLines = Int(truncating: numberOfLines)
            invalidateIntrinsicContentSize()
        }
    }

    @objc var width: NSNumber = 0 {
        didSet {
            label.customWidth = CGFloat(truncating: width)
            invalidateIntrinsicContentSize()
        }
    }

    // MARK: - Hybrid Method

    @objc func measureDimensions() -> [String: NSNumber] {
        let size = intrinsicContentSize
        return [
            "width": NSNumber(value: Double(size.width)),
            "height": NSNumber(value: Double(size.height))
        ]
    }

    // MARK: - Font Resolution (THE FIX)

    private func updateFont() {
        let size = CGFloat(truncating: fontSize)

        let (family, weight, italic) = parseFontFamily(fontFamily)
        let resolvedName = resolveFontName(family: family, weight: weight, italic: italic)

        let cacheKey = "\(resolvedName)-\(size)"
        if let cached = fontCache[cacheKey] {
            label.font = cached
        } else {
            let font =
                UIFont(name: resolvedName, size: size)
                ?? UIFont.systemFont(ofSize: size)

            fontCache[cacheKey] = font
            label.font = font
        }

        invalidateIntrinsicContentSize()
    }

    /// Extracts trailing numeric weight
    /// "Montserrat-900" → ("Montserrat", 900)
    private func parseFontFamily(_ raw: String) -> (family: String, weight: Int, italic: Bool) {
        let parts = raw.lowercased().split(separator: "-")

        var family = parts.first.map(String.init) ?? raw
        var weight = 400
        var italic = false

        for part in parts.dropFirst() {
            if part == "italic" {
                italic = true
            } else if let w = Int(part) {
                weight = w
            }
        }

        return (family, weight, italic)
    }


    /// Maps numeric weight → iOS PostScript name
    private func resolveFontName(family: String, weight: Int, italic: Bool) -> String {
         let singleFaceFonts: Set<String> = [
            "bebasneue",
            "bebas neue"
        ]

        if singleFaceFonts.contains(family.lowercased()) {
            return "\(family)-Regular"
        }
        let base: String
        switch weight {
            case 100: base = "Thin"
            case 200: base = "ExtraLight"
            case 300: base = "Light"
            case 400: base = "Regular"
            case 500: base = "Medium"
            case 600: base = "SemiBold"
            case 700: base = "Bold"
            case 800: base = "ExtraBold"
            case 900: base = "Black"
            default:  base = "Regular"
        }

        if italic {
            if base == "Regular" {
                return "\(family)-Italic"
            }
            return "\(family)-\(base)Italic"
        }

        return "\(family)-\(base)"
    }

    // MARK: - Color Helper

    private func colorStringToUIColor(_ colorString: String) -> UIColor {
        let cString = colorString
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .lowercased()

        switch cString {
        case "black": return .black
        case "darkgray", "darkgrey": return .darkGray
        case "lightgray", "lightgrey": return .lightGray
        case "white": return .white
        case "gray", "grey": return .gray
        case "red": return .red
        case "green": return .green
        case "blue": return .blue
        case "cyan": return .cyan
        case "yellow": return .yellow
        case "magenta": return .magenta
        case "orange": return .orange
        case "purple": return .purple
        case "brown": return .brown
        case "clear": return .clear
        default: break
        }

        var hex = cString.uppercased()
        if hex.hasPrefix("#") {
            hex.removeFirst()
        }

        var rgbValue: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&rgbValue)

        if hex.count == 6 {
            return UIColor(
                red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255,
                green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255,
                blue: CGFloat(rgbValue & 0x0000FF) / 255,
                alpha: 1
            )
        }

        if hex.count == 8 {
            return UIColor(
                red: CGFloat((rgbValue & 0xFF000000) >> 24) / 255,
                green: CGFloat((rgbValue & 0x00FF0000) >> 16) / 255,
                blue: CGFloat((rgbValue & 0x0000FF00) >> 8) / 255,
                alpha: CGFloat(rgbValue & 0x000000FF) / 255
            )
        }

        return .black
    }
}
