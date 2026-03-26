import UIKit
import NitroModules

// MARK: - KeyboardKeyButton

class KeyboardKeyButton: UIButton {
    private let visualView = UIView()

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupVisualView()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupVisualView()
    }

    private func setupVisualView() {
        visualView.isUserInteractionEnabled = false
        visualView.backgroundColor = UIColor(hex: "#3A3A3A")
        visualView.layer.cornerRadius = 5
        visualView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(visualView)
        sendSubviewToBack(visualView)
        
        // 4pt padding on all sides creates 8pt gap between keys
        NSLayoutConstraint.activate([
            visualView.topAnchor.constraint(equalTo: topAnchor, constant: 4),
            visualView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -4),
            visualView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 4),
            visualView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -4)
        ])
    }

    override var isHighlighted: Bool {
        didSet {
            visualView.backgroundColor = isHighlighted ? UIColor(hex: "#777777") : UIColor(hex: "#3A3A3A")
        }
    }
}

// MARK: - UIColor Hex Extension

extension UIColor {
    convenience init(hex: String) {
        var hexSanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")

        var rgb: UInt64 = 0

        var r: CGFloat = 0.0
        var g: CGFloat = 0.0
        var b: CGFloat = 0.0
        var a: CGFloat = 1.0

        let length = hexSanitized.count

        guard Scanner(string: hexSanitized).scanHexInt64(&rgb) else {
            self.init(red: 1, green: 1, blue: 1, alpha: 1)
            return
        }

        if length == 6 {
            r = CGFloat((rgb & 0xFF0000) >> 16) / 255.0
            g = CGFloat((rgb & 0x00FF00) >> 8) / 255.0
            b = CGFloat(rgb & 0x0000FF) / 255.0

        } else if length == 8 {
            r = CGFloat((rgb & 0xFF000000) >> 24) / 255.0
            g = CGFloat((rgb & 0x00FF0000) >> 16) / 255.0
            b = CGFloat((rgb & 0x0000FF00) >> 8) / 255.0
            a = CGFloat(rgb & 0x000000FF) / 255.0
        }

        self.init(red: r, green: g, blue: b, alpha: a)
    }
}

// MARK: - MatiksKeyboardUIView

class MatiksKeyboardUIView: UIView {
    
    var onKeyInputCallback: ((String) -> Void)?
    var onDeleteCallback: (() -> Void)?
    var hapticsEnabled: Bool = false

    private var _customKeyboardType: KEYBOARD_TYPE = .numbers
    private var _keyboardLayout: KEYBOARD_LAYOUT = .telephone
    
    var customKeyboardType: KEYBOARD_TYPE {
        get { return _customKeyboardType }
        set {
            if _customKeyboardType != newValue {
                _customKeyboardType = newValue
                setupKeyboard()
            }
        }
    }
    
    var keyboardLayout: KEYBOARD_LAYOUT {
        get { return _keyboardLayout }
        set {
            if _keyboardLayout != newValue {
                _keyboardLayout = newValue
                setupKeyboard()
            }
        }
    }

    private let stackView: UIStackView = {
        let sv = UIStackView()
        sv.axis = .vertical
        sv.distribution = .fillEqually
        sv.spacing = 0
        sv.translatesAutoresizingMaskIntoConstraints = false
        sv.isLayoutMarginsRelativeArrangement = true
        sv.layoutMargins = UIEdgeInsets(top: 8, left: 8, bottom: 8, right: 8)
        return sv
    }()
    
    private let TELEPHONE_KEYS = [
        ["1", "2", "3"],
        ["4", "5", "6"],
        ["7", "8", "9"],
        [".", "0", "delete"]
    ]
    
    private let PHONE_ABILITY_KEYS = [
        ["1", "2", "3", "-"],
        ["4", "5", "6", "/"],
        ["7", "8", "9", "clr"],
        [".", "0", "space", "delete"]
    ]
    
    private let CALCULATOR_KEYS = [
        ["7", "8", "9"],
        ["4", "5", "6"],
        ["1", "2", "3"],
        [".", "0", "delete"]
    ]
    
    private let CALC_ABILITY_KEYS = [
        ["7", "8", "9", "-"],
        ["4", "5", "6", "/"],
        ["1", "2", "3", "clr"],
        [".", "0", "space", "delete"]
    ]

    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = UIColor(hex: "#292929")
        addSubview(stackView)
        
        NSLayoutConstraint.activate([
            stackView.topAnchor.constraint(equalTo: topAnchor),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor),
            stackView.leadingAnchor.constraint(equalTo: leadingAnchor),
            stackView.trailingAnchor.constraint(equalTo: trailingAnchor)
        ])
        
        setupKeyboard()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupKeyboard() {
        stackView.arrangedSubviews.forEach { $0.removeFromSuperview() }
        
        let keys: [[String]]
        if _keyboardLayout == .calculator {
            keys = _customKeyboardType == .numbersAndOperators ? CALC_ABILITY_KEYS : CALCULATOR_KEYS
        } else {
            keys = _customKeyboardType == .numbersAndOperators ? PHONE_ABILITY_KEYS : TELEPHONE_KEYS
        }
        
        for rowKeys in keys {
            let rowStack = UIStackView()
            rowStack.axis = .horizontal
            rowStack.distribution = .fillEqually
            rowStack.spacing = 0
            
            for key in rowKeys {
                let button = KeyboardKeyButton(type: .custom)
                
                if key == "delete" {
                    button.setTitle("⌫", for: .normal)
                } else {
                    button.setTitle(key, for: .normal)
                }
                
                button.setTitleColor(.white, for: .normal)
                button.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .regular)
                button.backgroundColor = .clear
                
                button.heightAnchor.constraint(greaterThanOrEqualToConstant: 52).isActive = true
                button.addTarget(self, action: #selector(handleButtonPress(_:)), for: .touchDown)
                
                button.accessibilityLabel = key
                
                rowStack.addArrangedSubview(button)
            }
            stackView.addArrangedSubview(rowStack)
        }
        
        // Enable multi-touch for rollover typing
        self.isMultipleTouchEnabled = true
        stackView.isUserInteractionEnabled = true
    }
    
    @objc private func handleButtonPress(_ sender: UIButton) {
        if hapticsEnabled {
            let generator = UIImpactFeedbackGenerator(style: .medium)
            generator.impactOccurred()
        }
        
        guard let key = sender.accessibilityLabel else { return }
        
        if key == "delete" {
            onDeleteCallback?()
        } else {
            onKeyInputCallback?(key)
        }
    }
}

// MARK: - HybridMatiksKeyboardView

class HybridMatiksKeyboardView: HybridMatiksKeyboardViewSpec {
    
    private let keyboardView = MatiksKeyboardUIView()
    
    // MARK: - HybridViewSpec
    
    public var view: UIView {
        return keyboardView
    }
    
    // MARK: - Props
    
    public var customKeyboardType: KEYBOARD_TYPE? {
        get { return keyboardView.customKeyboardType }
        set {
            keyboardView.customKeyboardType = newValue ?? .numbers
        }
    }
    
    public var keyboardLayout: KEYBOARD_LAYOUT? {
        get { return keyboardView.keyboardLayout }
        set {
            keyboardView.keyboardLayout = newValue ?? .telephone
        }
    }
    
    public var onKeyInput: ((_ event: KeyInputEvent) -> Void)? {
        didSet {
            keyboardView.onKeyInputCallback = { [weak self] key in
                self?.onKeyInput?(KeyInputEvent(key: key))
            }
        }
    }
    
    public var onDelete: ((_ event: DeleteEvent) -> Void)? {
        didSet {
            keyboardView.onDeleteCallback = { [weak self] in
                self?.onDelete?(DeleteEvent(deleted: true))
            }
        }
    }
    
    public var hapticsEnabled: Bool? {
        get { return keyboardView.hapticsEnabled }
        set {
            keyboardView.hapticsEnabled = newValue ?? false
        }
    }
    
    // MARK: - Init
    
    public override init() {
        super.init()
    }
}
