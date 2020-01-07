# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.


set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_STANDARD 17)

set (HEADERS
        Demo.hpp
        manager/DirectoryManager.h
        manager/Tok.h
        model/APIInfo.h
        model/CoverArt.h
        model/Song.h
        model/Token.h
        model/User.h
        repository/APIRepository.h
        repository/BaseRepository.h
        repository/CoverArtRepository.h
        repository/RepeatRepository.h
        repository/ShuffleRepository.h
        repository/SongRepository.h
        repository/TokenRepository.h
        repository/UserRepository.h
        types/ConnType.h
        types/RepeatTypes.h
        types/ShuffleTypes.h
        utility/GeneralUtility.h
)

set(JSON_BuildTests OFF CACHE INTERNAL "")


add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/json)
add_subdirectory(${CMAKE_CURRENT_LIST_DIR}/3rdparty/SQLiteCpp)


add_library(native-lib SHARED
        Demo.cpp
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

include_directories(${CURL_INCLUDE_DIR} ${SQLITECPP_INCLUDE} ${HEADERS})

target_link_libraries(native-lib PRIVATE
        nlohmann_json::nlohmann_json
        boo
        SQLiteCpp
        sqlite3
        dl
        ${log-lib}
        z
)