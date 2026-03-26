import React, { useEffect, useRef, useState } from "react";
import { callback, getHostComponent } from "react-native-nitro-modules";
import MatiksKeyboardViewConfig from "../nitrogen/generated/shared/json/MatiksStrokeTextConfig.json";
import { type MatiksStrokeText, type MatiksStrokeTextMethods, type MatiksStrokeTextProps } from "./specs/Stroke.nitro";

const StrokeTextViewComponent = getHostComponent<MatiksStrokeTextProps, MatiksStrokeTextMethods>(
  'MatiksStrokeText',
  () => MatiksKeyboardViewConfig
)

interface StrokeTextViewProps extends MatiksStrokeTextProps {
  styles?: {
    width?: number,
    height?: number
  }
}

const StrokeTextView = ({ text, styles, ...props }: StrokeTextViewProps) => {
  const ref = useRef<MatiksStrokeText | null>(null);
  const [size, setSize] = useState({ width: 0, height: 0 });
  const [isReady, setIsReady] = useState(false)

  useEffect(() => {
    if (!ref.current) return;
    const result = ref.current.measureDimensions();
    setSize(result);
  }, [
    props.fontFamily,
    props.fontSize,
    text,
    isReady
  ]);

  return (
    <StrokeTextViewComponent
      {...props}
      text={String(text)}
      style={[{ width: size.width, height: size.height }, styles]}
      hybridRef={callback((_ref) => {
        if(ref.current) return
        ref.current = _ref;
        setIsReady(true)
      })}
    />
  );
};

export default React.memo(StrokeTextView)
