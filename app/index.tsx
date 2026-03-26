import Button from "matiks-button";
import React from "react";
import { Alert, Text } from "react-native";
import { SafeAreaProvider, SafeAreaView } from "react-native-safe-area-context";
import HybridMatiksStrokeText from "rn-stroke-text";

const App = () => {
  return (
    <SafeAreaProvider>
      <SafeAreaView style={{  justifyContent: "center", alignItems: "center", backgroundColor: "white" }}>
        <Button title="Button" onPress={() => {Alert.alert("Button Pressed")}} />
        <Text style={{ fontSize: 20, color: "red", width: "100%", }}>App</Text>
       {/* <HybridMatiksKeyboardView 
  style={{ width: "100%", height: 240 }} 
  customKeyboardType={KEYBOARD_TYPE.NUMBERS}
  keyboardLayout={KEYBOARD_LAYOUT.TELEPHONE}
  hapticsEnabled={true}
  onKeyInput={ callback(({ key }) => {
    console.log("Key pressed:", key);
  })}
 
/> */}
<HybridMatiksStrokeText 
      text="Hello World"
      // width={200}
      fontSize={20}
      color="black"
      strokeColor="red"
      strokeWidth={0.5}
      fontFamily="Arial"
      align="center"
      numberOfLines={1}
    />
      </SafeAreaView>
    </SafeAreaProvider>
  );
};

export default App;
