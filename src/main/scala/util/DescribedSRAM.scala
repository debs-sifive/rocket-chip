// See LICENSE.Berkeley for license details.
// See LICENSE.SiFive for license details.

package freechips.rocketchip.util

import chisel3.internal.InstanceId
import freechips.rocketchip.util.Annotated
import freechips.rocketchip.diplomacy.DiplomaticSRAM
import chisel3.{Data, SyncReadMem, Vec}
import chisel3.util.log2Ceil
import freechips.rocketchip.amba.axi4.AXI4RAM
import freechips.rocketchip.diplomaticobjectmodel.DiplomaticObjectModelAddressing
import freechips.rocketchip.diplomaticobjectmodel.model.{OMSRAM, OMRTLModule}

import scala.math.log10

object DescribedSRAMIdAssigner {
  private var nextId: Int = 0
  def genId(): Int = this.synchronized {
    val id = nextId
    nextId += 1
    id
  }
}

object DescribedSRAM {
  def apply[T <: Data](
    name: String,
    desc: String,
    size: BigInt, // depth
    data: T
  ): (SyncReadMem[T], OMSRAM) = {

    val mem = SyncReadMem(size, data)

    mem.suggestName(name)

    val granWidth = data match {
      case v: Vec[_] => v.head.getWidth
      case d => d.getWidth
    }

    val uid = 0

    val omSRAM = DiplomaticObjectModelAddressing.makeOMSRAM(
      desc = desc,
      width = data.getWidth,
      depth = size,
      granWidth = granWidth,
      uid = uid,
      rtlModule = OMRTLModule(moduleName=name)
    )

    Annotated.srams(
      component = mem,
      name = name,
      address_width = log2Ceil(size),
      data_width = data.getWidth,
      depth = size,
      description = desc,
      write_mask_granularity = granWidth,
      uid = DescribedSRAMIdAssigner.genId())

    (mem, omSRAM)
  }
}
