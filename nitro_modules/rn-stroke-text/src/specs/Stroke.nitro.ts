type TextAlign = "center" | "left" | "right"
import type {
    HybridView,
    HybridViewMethods,
    HybridViewProps,
} from 'react-native-nitro-modules';

export interface MatiksStrokeTextProps extends HybridViewProps {
  width?: number;
  text: string;
  fontSize?: number;
  color?: string;
  strokeColor?: string;
  strokeWidth?: number;
  fontFamily?: string;
  align?: TextAlign;
  numberOfLines?: number;
  ellipsis?: boolean;
}


export interface Dimensions {
  width: number;
  height: number;
}

export interface MatiksStrokeTextMethods extends HybridViewMethods {
  measureDimensions(): Dimensions;
}

export type MatiksStrokeText = HybridView<
  MatiksStrokeTextProps,
  MatiksStrokeTextMethods
>
