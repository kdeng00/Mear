//
// Created by brahmix on 9/26/19.
//

#ifndef MEAR_SONG_H
#define MEAR_SONG_H

#include <string>


namespace model {

struct Song
{
    int id;
    std::string title;
    std::string artist;
    std::string album;
    std::string genre;
    int duration;
    int year;
};
}

#endif //MEAR_SONG_H
