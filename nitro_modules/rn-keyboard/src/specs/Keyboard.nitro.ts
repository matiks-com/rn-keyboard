export enum KEYBOARD_TYPE {
  NUMBERS = 'NUMBERS',
  NUMBERS_AND_OPERATORS = 'NUMBERS_AND_OPERATORS',
}

export enum KEYBOARD_LAYOUT {
  TELEPHONE = 'TELEPHONE',
  CALCULATOR = 'CALCULATOR',
}

/* ✅ Event payloads MUST be non-empty */

export interface KeyInputEvent {
  key: string
}

export interface DeleteEvent {
  deleted: true
}

import type {
    HybridView,
    HybridViewMethods,
    HybridViewProps,
} from 'react-native-nitro-modules'

export interface MatiksKeyboardViewProps extends HybridViewProps {
  customKeyboardType?: KEYBOARD_TYPE
  keyboardLayout?: KEYBOARD_LAYOUT

  onKeyInput?: (event: KeyInputEvent) => void
  onDelete?: (event: DeleteEvent) => void

  hapticsEnabled?: boolean
}

export interface MatiksKeyboardViewMethods extends HybridViewMethods {}

export type MatiksKeyboardView = HybridView<
  MatiksKeyboardViewProps,
  MatiksKeyboardViewMethods
>
