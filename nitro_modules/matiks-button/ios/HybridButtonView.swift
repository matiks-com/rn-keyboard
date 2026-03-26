import UIKit
import NitroModules

class HybridButtonView: HybridButtonViewSpec {

  private let button = UIButton(type: .system)

  // Props
  public var title: String = "" {
    didSet {
      button.setTitle(title, for: .normal)
    }
  }

  public var onPress: () -> Void = {}

  // Required by Nitro
  public var view: UIView {
    return button
  }

  public override init() {
    super.init()

    button.addTarget(
      self,
      action: #selector(handlePress),
      for: .touchUpInside
    )
  }

  @objc private func handlePress() {
    onPress()
  }
}
