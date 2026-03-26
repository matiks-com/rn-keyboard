import type {
    HybridView,
    HybridViewMethods,
    HybridViewProps,
} from 'react-native-nitro-modules'

/**
 * Props coming FROM React → Native
 */
export interface ButtonProps extends HybridViewProps {
  title: string
  onPress: () => void
}

/**
 * Methods callable FROM JS → Native
 * (empty for now)
 */
export interface ButtonMethods extends HybridViewMethods {}

/**
 * Final View type
 */
export type ButtonView = HybridView<ButtonProps, ButtonMethods>
