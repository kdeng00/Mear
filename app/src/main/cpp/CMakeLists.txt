# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.6.0)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.


# set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)

set(CMAKE_CXX11_EXTENSION_COMPILE_OPTION "-std=c++17")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++17")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS} -g -O0 -std=c++17")
set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS} -O3 -std=c++17 -DNDEBUG")

set (SOURCES
        APIRepository.cpp
        BaseRepository.cpp
        CoverArtRepository.cpp
        Demo.cpp
        RepeatRepository.cpp
        ShuffleRepository.cpp
        SongRepository.cpp
        Tok.cpp
        TokenRepository.cpp
        UserRepository.cpp
)

set (HEADERS
        APIRepository.h
        BaseRepository.h
        model/CoverArt.h
        CoverArtRepository.h
        Demo.h
        model/APIInfo.h
        model/Song.h
        model/Token.h
        model/User.h
        RepeatRepository.h
        RepeatTypes.h
        ShuffleRepository.h
        ShuffleTypes.h
        SongRepository.h
        Tok.h
        TokenRepository.h
        UserRepository.h
)

set(JSON_BuildTests OFF CACHE INTERNAL "")


add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/json)
add_subdirectory(${CMAKE_CURRENT_LIST_DIR}/3rdparty/SQLiteCpp)


add_library(native-lib SHARED
        ${SOURCES}
        ${HEADERS}
)


find_library(log-lib
        log
)


set (CURL_LIBRARY
        ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/android/${ANDROID_ABI}
)


set (CURL_INCLUDE_DIR
        ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/android/include/curl
        ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/android/include
)

set(SQLITECPP_INCLUDE
        ${CMAKE_CURRENT_LIST_DIR}/3rdparty/SQLiteCpp/include
)

add_library(boo STATIC IMPORTED)

set_target_properties(boo PROPERTIES IMPORTED_LOCATION
        "${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/android/${ANDROID_ABI}/libcurl.a"
)

include_directories(${CURL_INCLUDE_DIR} ${SQLITECPP_INCLUDE})

target_link_libraries(native-lib PRIVATE
        nlohmann_json::nlohmann_json
        boo
        SQLiteCpp
        sqlite3
        dl
        ${log-lib}
        z
)