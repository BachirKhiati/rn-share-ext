import React from 'react-native'

const ReactNativeShareExtension = React.NativeModules.ReactNativeShareExtension
export default {
  getSharedText: () => {
    return new Promise((resolve, reject) => {
      ReactNativeShareExtension.getSharedText(resolve, reject)
    })
  },
  clearSharedText: () =>
    ReactNativeShareExtension.clearSharedText(),
  data: () => ReactNativeShareExtension.data(),
  close: () => ReactNativeShareExtension.close(),
  clear: () => ReactNativeShareExtension.clear()
}
