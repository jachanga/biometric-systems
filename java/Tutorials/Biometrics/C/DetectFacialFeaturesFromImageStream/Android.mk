# ==========================================================
# NOTE:
# when executing binary, set LD_LIBRARY_PATH in device
# ==========================================================

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

BASEDIR := ../../..
DISTDIR := ../../../..

LOCAL_MODULE := DetectFacialFeaturesFromImageStream

LOCAL_ARM_MODE := arm


# Includes
LOCAL_C_INCLUDES += \
	$(BASEDIR)/Common/C \
	$(DISTDIR)/Include

# Source files
LOCAL_SRC_FILES := \
	DetectFacialFeaturesFromImageStream.c


LOCAL_LDLIBS += \
	-Wl,-z,muldefs -Wl,-rpath=$(DISTDIR)/Lib/Android/$(TARGET_ARCH_ABI) \
	-L $(DISTDIR)/Lib/Android/$(TARGET_ARCH_ABI) \
	-lNLicensing  -lNBiometricClient -lNBiometrics -lNDevices -lNMedia -lNCore -lNLicensing

include $(BUILD_EXECUTABLE)
