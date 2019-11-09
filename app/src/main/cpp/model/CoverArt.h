//
// Created by brahmix on 11/8/19.
//

#ifndef MEAR_COVERART_H
#define MEAR_COVERART_H

#include <string>

namespace model {
    class CoverArt {
    public:
        CoverArt() = default;
        CoverArt(const int id, const std::string& title) :
                id(id), title(title) { }

        int id;
        std::string title;
    };
}

#endif //MEAR_COVERART_H
