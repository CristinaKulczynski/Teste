import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;

public class TestOracle2 {
    public static void main(String[] args) {
        final String[] sql = new String[1];

        // Configuração dos parâmetros de autenticação
        String user = "system"; // Substitua pelo seu usuário do Oracle
        String passwd = "fefe1389"; // Substitua pela sua senha do Oracle

        try {
            String url = "jdbc:oracle:thin:@//localhost:1521/xe"; // Substitua de acordo com as suas configurações

            // Abre-se a conexão com o Banco de Dados
            Connection con = DriverManager.getConnection(url, user, passwd);

            // Cria-se Statement com base na conexão con
            Statement stmt = con.createStatement();

            // Verifica se a tabela já existe
            ResultSet tables = con.getMetaData().getTables(null, null, "USUARIOS", null);
            if (!tables.next()) {
                // Exemplo: criação da tabela de usuários com gatilho para auto incrementar o ID
                sql[0] = "CREATE TABLE usuarios ("
                        + "id INT PRIMARY KEY,"
                        + "nome VARCHAR(100) NOT NULL,"
                        + "endereco VARCHAR(100) NOT NULL)";
                stmt.executeUpdate(sql[0]);

                // Exemplo: criação do gatilho para auto incrementar o ID
                sql[0] = "CREATE OR REPLACE TRIGGER trg_usuarios BEFORE INSERT ON usuarios FOR EACH ROW "
                        + "BEGIN "
                        + "SELECT usuarios_seq.NEXTVAL INTO :new.id FROM dual; "
                        + "END;";
                stmt.executeUpdate(sql[0]);

                // Exemplo: inserindo dados na tabela de usuários
                sql[0] = "INSERT INTO usuarios (nome, endereco) VALUES ('João', 'Rua A, 123')";
                stmt.executeUpdate(sql[0]);

                sql[0] = "INSERT INTO usuarios (nome, endereco) VALUES ('Maria', 'Av. B, 456')";
                stmt.executeUpdate(sql[0]);

                sql[0] = "INSERT INTO usuarios (nome, endereco) VALUES ('Carlos', 'Rua C, 789')";
                stmt.executeUpdate(sql[0]);
            }


            // Criar e configurar a tela com tabela para mostrar usuários
            JFrame frame = new JFrame("Usuários");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setLayout(null);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Nome");
            model.addColumn("Endereço");

            JTable usuariosTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(usuariosTable);
            scrollPane.setBounds(50, 30, 300, 200);
            frame.add(scrollPane);

            JButton cadastrarButton = new JButton("Cadastrar Usuário");
            cadastrarButton.setBounds(50, 260, 150, 30);
            frame.add(cadastrarButton);

            JButton editarButton = new JButton("Editar Usuário");
            editarButton.setBounds(210, 260, 150, 30);
            frame.add(editarButton);

            JButton excluirButton = new JButton("Excluir Usuário");
            excluirButton.setBounds(50, 300, 310, 30);
            frame.add(excluirButton);

            // Ação do botão "Cadastrar Usuário"
            cadastrarButton.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        String nome = JOptionPane.showInputDialog(frame, "Digite o nome do usuário:");
                        // Verifica se o usuário pressionou "Cancelar" ou fechou a janela
                        if (nome != null) {
                            String endereco = JOptionPane.showInputDialog(frame, "Digite o endereço do usuário:");
                            // Verifica se o usuário pressionou "Cancelar" ou fechou a janela
                            if (endereco != null) {
                                sql[0] = "INSERT INTO usuarios (nome, endereco) VALUES ('" + nome + "', '" + endereco + "')";
                                stmt.executeUpdate(sql[0]);

                                JOptionPane.showMessageDialog(frame, "Usuário cadastrado com sucesso!");

                                // Atualiza a tabela após cadastrar
                                model.setRowCount(0);
                                ResultSet updatedRes = stmt.executeQuery("SELECT id, nome, endereco FROM usuarios");
                                while (updatedRes.next()) {
                                    int id = updatedRes.getInt("id");
                                    String updatedNome = updatedRes.getString("nome");
                                    String updatedEndereco = updatedRes.getString("endereco");
                                    model.addRow(new Object[]{id, updatedNome, updatedEndereco});
                                }
                            } else {
                                JOptionPane.showMessageDialog(frame, "Cadastro de usuário cancelado.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Cadastro de usuário cancelado.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // Ação do botão "Editar Usuário"
            editarButton.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        int selectedRow = usuariosTable.getSelectedRow();
                        if (selectedRow != -1) {
                            int userID = (int) usuariosTable.getValueAt(selectedRow, 0);
                            String novoNome = JOptionPane.showInputDialog(frame, "Digite o novo nome do usuário:");
                            String novoEndereco = JOptionPane.showInputDialog(frame, "Digite o novo endereço do usuário:");

                            sql[0] = "UPDATE usuarios SET nome = '" + novoNome + "', endereco = '" + novoEndereco + "' WHERE id = " + userID;
                            stmt.executeUpdate(sql[0]);

                            JOptionPane.showMessageDialog(frame, "Usuário editado com sucesso!");

                            // Atualiza a tabela após editar
                            model.setRowCount(0);
                            ResultSet updatedRes = stmt.executeQuery("SELECT id, nome, endereco FROM usuarios");
                            while (updatedRes.next()) {
                                int id = updatedRes.getInt("id");
                                String updatedNome = updatedRes.getString("nome");
                                String updatedEndereco = updatedRes.getString("endereco");
                                model.addRow(new Object[]{id, updatedNome, updatedEndereco});
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Selecione um usuário para editar.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // Ação do botão "Excluir Usuário"
            excluirButton.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    try {
                        int selectedRow = usuariosTable.getSelectedRow();
                        if (selectedRow != -1) {
                            int userID = (int) usuariosTable.getValueAt(selectedRow, 0);

                            sql[0] = "DELETE FROM usuarios WHERE id = " + userID;
                            stmt.executeUpdate(sql[0]);

                            JOptionPane.showMessageDialog(frame, "Usuário excluído com sucesso!");

                            // Atualiza a tabela após excluir
                            model.setRowCount(0);
                            ResultSet updatedRes = stmt.executeQuery("SELECT id, nome, endereco FROM usuarios");
                            while (updatedRes.next()) {
                                int id = updatedRes.getInt("id");
                                String updatedNome = updatedRes.getString("nome");
                                String updatedEndereco = updatedRes.getString("endereco");
                                model.addRow(new Object[]{id, updatedNome, updatedEndereco});
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Selecione um usuário para excluir.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // Consulta e exibição de todos os usuários disponíveis na tabela
            ResultSet res = stmt.executeQuery("SELECT id, nome, endereco FROM usuarios");
            while (res.next()) {
                int id = res.getInt("id");
                String nome = res.getString("nome");
                String endereco = res.getString("endereco");
                model.addRow(new Object[]{id, nome, endereco});
            }

            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
