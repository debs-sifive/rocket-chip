// See LICENSE.Berkeley for license details.
// See LICENSE.SiFive for license details.

package freechips.rocketchip.util

import chisel3._

// this bundle is used to expose some internal core signals
// to verification monitors which sample instruction commits
class CoreMonitorBundle(val xLen: Int) extends Bundle with Clocked {
  val cease = Bool()
  val reg_mscratch = UInt(width = xLen.W)
}

// mark a module that has cores with CoreMonitorBundles
trait HasCoreMonitorBundles {
    def coreMonitorBundles: List[CoreMonitorBundle]
}
