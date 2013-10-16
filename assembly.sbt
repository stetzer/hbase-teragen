import AssemblyKeys._

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("META-INF", "MANIFEST.MF", xs @ _*) => MergeStrategy.discard
    case PathList(ps @ _*) => MergeStrategy.first
    case x => old(x)
  }
}
