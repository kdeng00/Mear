using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

using SQLite;

namespace Mear.Models
{
    [DataContract]
    [Table("MusicControls")]
    public class MusicControls
    {
        [PrimaryKey, Column("Id"), AutoIncrement]
        public int Id { get; set; }
        public bool ShuffleOn { get; set; } = false;
        public bool RepeatOn { get; set; } = false;
    }
}
