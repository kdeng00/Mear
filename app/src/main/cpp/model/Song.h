//
// Created by brahmix on 9/26/19.
//

#ifndef MEAR_SONG_H
#define MEAR_SONG_H

#include <string>
#include <vector>


namespace model {

class Song
{
public:
    Song() = default;
    Song(const int id) : id(id) { }
    Song(const int id, const std::string& title, const std::string& artist,
            const std::string& album, const std::string& genre,
            const int duration, const int year) :
            id(id), title(title), artist(artist), album(album),
            genre(genre), duration(duration), year(year) { }
    Song(const int id, const std::string& title, const std::string& artist,
         const std::string& album, const std::string& albumArtist, const std::string& genre,
         const int duration, const int year, const int coverArtId) :
            id(id), title(title), artist(artist), album(album), albumArtist(albumArtist),
            genre(genre), duration(duration), year(year), coverArtId(coverArtId) { }
    /**
Song(const int id, const std::string&& title, const std::string&& artist,
 const std::string&& album, const std::string&& genre,
 const int duration, const int year) :
    id(id), title(std::move(title)), artist(std::move(artist)),
    album(std::move(album)), genre(std::move(genre)), duration(duration),
    year(year) { }
     */

    int id;
    std::string title;
    std::string artist;
    std::string album;
    std::string albumArtist;
    std::string genre;
    int duration;
    int year;
    int coverArtId;
    std::vector<char> data;
};
}

#endif //MEAR_SONG_H
