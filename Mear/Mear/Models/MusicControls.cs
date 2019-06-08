using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

using SQLite;

using Mear.Models.PlayerControls;

namespace Mear.Models
{
    [DataContract]
    [Table("MusicControls")]
    public class MusicControls
    {
        [PrimaryKey, Column("Id"), AutoIncrement]
        public int Id { get; set; }
        public bool ShuffleOn { get; set; } = false;
        public int RepeatOn { get; set; } = (int)PlayerControls.Repeat.OFF;
    }
}
