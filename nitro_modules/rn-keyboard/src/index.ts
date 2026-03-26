import { getHostComponent } from "react-native-nitro-modules";
import MatiksKeyboardViewConfig from "../nitrogen/generated/shared/json/MatiksKeyboardViewConfig.json";
import { KEYBOARD_LAYOUT, KEYBOARD_TYPE, type MatiksKeyboardViewMethods, type MatiksKeyboardViewProps } from "./specs/Keyboard.nitro";

export type KeyboardProps = MatiksKeyboardViewProps
export type KeyboardMethods = MatiksKeyboardViewMethods

export { KEYBOARD_LAYOUT, KEYBOARD_TYPE };

const Keyboard = getHostComponent<MatiksKeyboardViewProps, MatiksKeyboardViewMethods>(
  'MatiksKeyboardView',
  () => MatiksKeyboardViewConfig
)

export default Keyboard
