using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using System.Text;

using SQLite;

namespace Mear.Models
{
    [DataContract]
    [Table("Settings")]
    public class Settings
    {
        [PrimaryKey, Column("Id"), AutoIncrement]
        public int Id { get; set; }
        public bool DarkTheme { get; set; } = true;
    }
}
