import React from 'react'
import { callback, getHostComponent } from 'react-native-nitro-modules'
import ButtonViewConfig from '../nitrogen/generated/shared/json/ButtonViewConfig.json'
import type { ButtonMethods, ButtonProps } from './specs/Button.nitro'

const Button = getHostComponent<ButtonProps, ButtonMethods>(
  'ButtonView',
  () => ButtonViewConfig
)

const ButtonComponent = ({ title, onPress }: ButtonProps) => {
    return <Button title={title} onPress={callback(onPress)} style={{ width: 200, height: 50 }} />
}

export default ButtonComponent