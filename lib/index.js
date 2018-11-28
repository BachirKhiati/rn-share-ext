var { NativeModules } = require('react-native');
module.exports = NativeModules.ReactNativeShareExtension;
export default {
    getSharedText: () => NativeModules.ReactNativeShareExtension.getSharedText(),
    getSharedText: () => NativeModules.ReactNativeShareExtension.clearSharedText(),
    data: () => NativeModules.ReactNativeShareExtension.data(),
    close: () => NativeModules.ReactNativeShareExtension.close(),
    clear: () => NativeModules.ReactNativeShareExtension.clear(),
  }
