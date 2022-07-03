# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

if(NOT DEFINED VCPKG_CHAINLOAD_TOOLCHAIN_FILE)
    set(VCPKG_CHAINLOAD_TOOLCHAIN_FILE "${CMAKE_TOOLCHAIN_FILE}")
endif()

if(NOT DEFINED CMAKE_TOOLCHAIN_FILE)
    set(TOOLCHAIN_PATH ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/vcpkg/scripts/buildsystems/vcpkg.cmake)

    set(CMAKE_TOOLCHAIN_FILE
            "${TOOLCHAIN_PATH}"
            CACHE STRING "Vcpkg toolchain file")

    message("Using default toolchain file")
endif()

project("mear")


message("Android ABI ${ANDROID_ABI}")
message("Android Platform ${ANDROID_PLATFORM}")
message("Toolchain file ${CMAKE_TOOLCHAIN_FILE}")
message("Android toolchain file ${VCPKG_CHAINLOAD_TOOLCHAIN_FILE}")
message("VCPKG target triplet ${VCPKG_TARGET_TRIPLET}")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        mear

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_package(nlohmann_json CONFIG REQUIRED)
# target_link_libraries(main PRIVATE nlohmann_json nlohmann_json::nlohmann_json)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        mear

        PRIVATE nlohmann_json nlohmann_json::nlohmann_json

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})