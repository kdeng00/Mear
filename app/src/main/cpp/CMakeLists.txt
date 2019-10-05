# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.


# set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)

set(CMAKE_CXX11_EXTENSION_COMPILE_OPTION "-std=c++17")

set (SOURCES
        Demo.cpp
        SongRepository.cpp
        Tok.cpp
)

set (HEADERS
        model/Song.h
        model/User.h
        SongRepository.h
        Tok.h
)

set(JSON_BuildTests OFF CACHE INTERNAL "")


add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/json)


add_library(native-lib SHARED

        # Provides a relative path to your source file(s).
        ${SOURCES}
        ${HEADERS}
)


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library(log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log
)


set (CURL_LIBRARY
        ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/android/${ANDROID_ABI}
)

set (CURL_INCLUDE_DIR
        ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/android/include/curl
        ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/android/include
)

add_library(boo STATIC IMPORTED)
set_target_properties(boo PROPERTIES IMPORTED_LOCATION
        "${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/android/${ANDROID_ABI}/libcurl.a"
)

include_directories(${CURL_INCLUDE_DIR})

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries(native-lib PRIVATE

        nlohmann_json::nlohmann_json
        boo
        ${log-lib}
        z
)