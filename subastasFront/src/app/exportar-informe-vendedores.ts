import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
import { InformeVendedor } from './informe-vendedor';

export function exportarReporteVendedoresPDF(reporte: InformeVendedor[]) {
  const doc = new jsPDF();

  doc.text('Informe de Rendimiento de Vendedores', 14, 15);

  autoTable(doc, {
    startY: 20,
    head: [['ID Vendedor', 'Subastas Finalizadas', 'Vendidas', 'Total Vendido', 'Promedio por Venta']],
    body: reporte.map(r => [
      r.idVendedor,
      r.subastasFinalizadas,
      r.subastasVendidas,
      `$${r.totalVendido.toFixed(2)}`,
      `$${r.promedioVenta.toFixed(2)}`
    ])
  });

  doc.save('informe-vendedores.pdf');
}